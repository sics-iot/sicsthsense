/*
 * Copyright (c) 2013, Swedish Institute of Computer Science
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of The Swedish Institute of Computer Science nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE SWEDISH INSTITUTE OF COMPUTER SCIENCE BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package logic

import com.avaje.ebean.Expr
import models.User
import models.Vfile
import play.api.Logger
import play.db.ebean.Transactional
import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.mutable
import scalax.file.Path

object FileSystem {
  private val logger = Logger(this.getClass)

  def createError {
  }

  def listFiles(user: User): java.util.List[Vfile] = {
    Argument.notNull(user)

    return Vfile.find.where.eq("owner_id", user.id).orderBy("path").findList
  }

  /** LS into a dir */
  def lsDir(user: User, path: String): java.util.List[Vfile] = {
    Argument.notNull(user)
    Argument.absolutePath(path)

    val p =
      if (path.endsWith("/")) path
      else path + "/"

    return Vfile.find
      .where(
      Expr.and(
        Expr.eq("owner_id", user.id),
        Expr.and(
          Expr.startsWith("path", p),
          Expr.not(Expr.like("path", p + "%/%"))
        )
      )
    )
      .orderBy("type")
      .orderBy("path")
      .findList
  }

  def listHTMLFileSystem(user: User): String = {
    Argument.notNull(user)

    val sb: StringBuffer = new StringBuffer
    val files: mutable.Buffer[Vfile] = Vfile.find.where.eq("owner_id", user.id).orderBy("path asc").findList

    var prevdepth: Int = 0
    var prevdirs: Array[String] = Array()

    // Loop through all files and directories but ignore the root
    for { f <- files if f.getPath != "/" } {
      // Split the path segments and calculate the depth of the file
      val dirs: Array[String] = f.getPath.split("/")
      val depth: Int = dirs.length - 1

      // Count how many path segments the current file shares with the previous one
      val sharedAncestors = (dirs, prevdirs).zipped.takeWhile {
        case (a, b) => a == b
      }.size

      // For all segments that the previous file had more than we, close the <li>
      for (i <- sharedAncestors until prevdepth) {
        sb.append("</ul></li>\n")
      }

      // Remember current stats for the next iteration
      prevdirs = dirs
      prevdepth = depth

      // If the current file is a directory then create a new directory node with its name
      if (f.isDir) {
        sb.append("<li class='jstree-open'><i class='icon-folder-open hideFolder'></i><span class='dirNode' data-filepath='")
        sb.append(f.getPath)
        sb.append("'> ")
        sb.append(dirs(depth))
        sb.append("</span>\n<ul class='folderNodeUL'>\n")
      } else {
        // If the current file is a file then decrement the depth for the next iteration so
        // that it does not assume this file was a directory.
        prevdepth -= 1
        sb.append("<li class='jstree-leaf'><i class='icon-file'></i><span class='fileNode' data-filepath='")
        sb.append(f.getPath)
        sb.append("'> ")
        sb.append(dirs(depth))
        sb.append("</span></li>\n")
      }
    }

    return sb.toString
  }

  @Transactional
  private def ensurePath(user: User, path: String): Unit = {
    Argument.notNull(user)
    Argument.absolutePath(path)

    val p = Path.fromString(path)

    logger.info(s"Creating path $path")

    for (parent <- p.parents.reverse :+ p) {
      if (!exists(user, parent.path)) {
        Vfile.create(new Vfile(user, parent.path, Vfile.Filetype.DIR))
      } else {
        val file = read(user, parent.path)

        if (file.isDir) {
          // Do nothing, everything is fine
        } else if (file.isFile) {
          throw new IllegalArgumentException(
            s"Path $path could not be created because the parent ${parent.path} already exists and is a File"
          )
        } else {
          throw new IllegalStateException(
            s"The FileSystem is broken, path ${parent.path} exists but is neither file nor directory"
          )
        }
      }
    }
  }

  @Transactional
  def createFile(user: User, path: String): Vfile = {
    Argument.notNull(user)
    Argument.absolutePath(path)

    Path.fromString(path).parent match {
      case Some(parent) => ensurePath(user, parent.path)
      case None => // Do nothing, the parent is root
    }

    val f: Vfile = Vfile.create(new Vfile(user, path, Vfile.Filetype.FILE))
    logger.info("add file: " + path)

    return f
  }

  @Transactional
  def createDirectory(user: User, path: String): Vfile = {
    Argument.notNull(user)
    Argument.absolutePath(path)
    Argument.requireNot(exists(user, path), s"Path '$path' should not exist")

    ensurePath(user, path)

    return read(user, path)
  }

  def read(user: User, path: String): Vfile = {
    Argument.notNull(user)
    Argument.absolutePath(path)

    return Vfile.find
      .where
      .eq("owner_id", user.id)
      .eq("path", path)
      .findUnique()
  }

  def exists(user: User, path: String): Boolean = {
    Argument.notNull(user)
    Argument.absolutePath(path)

    return Vfile.find
      .where.eq("owner_id", user.id)
      .eq("path", path)
      .findRowCount() == 1
  }

  def isDir(user: User, path: String): Boolean = {
    Argument.notNull(user)
    Argument.absolutePath(path)

    return Vfile.find
      .where
      .eq("owner_id", user.id)
      .eq("path", path)
      .eq("type", Vfile.Filetype.DIR)
      .findRowCount() == 1
  }

  def isEmptyDir(user: User, path: String): Boolean = {
    Argument.notNull(user)
    Argument.absolutePath(path)

    if (!isDir(user, path)) {
      return false
    }

    return Vfile.find
      .where
      .eq("owner_id", user.id)
      .startsWith("path", path)
      .findRowCount() == 0
  }

  def isFile(user: User, path: String): Boolean = {
    Argument.notNull(user)
    Argument.absolutePath(path)

    return Vfile.find
      .where
      .eq("owner_id", user.id)
      .eq("path", path)
      .eq("type", Vfile.Filetype.FILE)
      .findRowCount() == 1
  }

  @Transactional
  def deleteFile(user: User, path: String): Boolean = {
    Argument.notNull(user)
    Argument.absolutePath(path)

    def delete(file: Vfile): Unit =
      if (file.isDir)
        for (child <- lsDir(user, file.getPath))
          delete(child)
      else if (file.isFile)
        file.delete()
      else
        throw new IllegalStateException(s"Deleting non existing file '$path' ")

    val f: Vfile = read(user, path)
    if (f != null) {
      delete(f)

      return true
    }
    else {
      logger.warn("Vfile path to delete does not exist:: " + path)

      return false
    }
  }

  @Transactional
  def moveFile(user: User, path: String, newPath: String): Boolean = {
    Argument.notNull(user)
    Argument.absolutePath(path)
    Argument.absolutePath(newPath)

    Argument.require(exists(user, path), s"Source path $path must exist to move file")
    Argument.requireNot(exists(user, newPath), s"Target path $newPath must not exist to move file")

    Path.fromString(newPath).parent match {
      case Some(parent) => ensurePath(user, parent.path)
      case None => // Do nothing
    }

    val file = read(user, path)

    file.setPath(newPath)
    file.save

    logger.info(s"File moved from '$path' to '$newPath'")

    if (file.isDir) {
      lsDir(user, path).map { child =>
        moveFile(user, child.getPath, child.getPath.replaceAll(s"^$path", newPath))
      }.fold(true)(_ && _)
    } else {
      true
    }
  }
}
