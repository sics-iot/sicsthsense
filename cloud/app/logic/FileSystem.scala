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
import java.util.Collections
import models.User
import models.Vfile
import play.Logger
import play.db.ebean.Transactional
import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.mutable

object FileSystem {
  def createError {
  }

  def listFiles(user: User): java.util.List[Vfile] = {
    Argument.notNull(user)

    return Vfile.find.where.eq("owner_id", user.id).orderBy("path").findList
  }

  /** LS into a dir */
  def lsDir(user: User, path: String): java.util.List[Vfile] = {
    Argument.notNull(user)
    Argument.notEmpty(path)

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
    var prevdepth: Int = 0
    var prevdirs: Array[String] = Array()
    val files: mutable.Buffer[Vfile] = Vfile.find.where.eq("owner_id", user.id).orderBy("path asc").findList

    for (f <- files) {
      val dirs: Array[String] = f.getPath.split("/")
      val depth: Int = dirs.length - 1

      val sharedAncestors = (dirs, prevdirs).zipped.takeWhile {
                                                                case (a, b) => a == b
                                                              }.size

      for (i <- sharedAncestors to prevdepth) {
        sb.append("</ul></li>\n")
      }

      prevdirs = dirs
      prevdepth = depth
      if (f.isDir) {
        sb.append("<li class='jstree-open'><i class='icon-folder-open hideFolder'></i><span class='dirNode' data-filepath='" + f.getPath + "'> " + dirs(dirs.length - 1) + "</span>\n<ul class='folderNodeUL'>\n")
      }
      else {
        prevdepth -= 1
        sb.append("<li class='jstree-leaf'><i class='icon-file'></i><span class='fileNode' data-filepath='" + f.getPath + "'> " + dirs(dirs.length - 1) + "</span></li>\n")
      }
    }
    return sb.toString
  }

  def addFile(user: User, path: String): Vfile = {
    Argument.notNull(user)
    Argument.notEmpty(path)

    Argument.requireNot(path.endsWith("/"), s"Path '$path' must not end in '/'")

    // Fancy way to write old school for (;;), Scala does not support this
    for (sep <- Stream.iterate(2)(sep => path.indexOf('/', sep + 1)).drop(1).takeWhile(_ > -1)) {
      val ancestors: String = path.substring(0, sep)

      if (!fileExists(user, ancestors)) {
        addDirectory(user, ancestors)
      }
      else if (isFile(user, ancestors)) {
        Logger.error(s"Path '$path' already exists as a file: $ancestors")
      }
      else if (isDir(user, ancestors)) {
      }
      else {
        Logger.info("File system broke! " + ancestors)
      }
    }

    val f: Vfile = Vfile.create(new Vfile(user, path, Vfile.Filetype.FILE))
    Logger.info("add file: " + path)

    return f
  }

  @Transactional def addDirectory(user: User, path: String): Vfile = {
    Argument.notNull(user)
    Argument.notEmpty(path)

    Argument.require(path.endsWith("/"), s"Path '$path' must end in '/'")
    Argument.requireNot(fileExists(user, path), s"'$path' Path should not exist")

    val lastSlash = path.lastIndexOf('/') // get parent path
    val parent = path.substring(0, lastSlash) // excludes last slash

    if (!fileExists(user, parent)) {
      addDirectory(user, parent)
    } else if (isFile(user, parent)) {
      Logger.error(s"Path '$path' already exists as a file: $parent")
    } else if (isDir(user, parent)) {
    }

    val dir: Vfile = Vfile.create(new Vfile(user, path, Vfile.Filetype.DIR))
    return dir
  }

  def fileExists(user: User, path: String): Boolean = {
    Argument.notNull(user)
    Argument.notEmpty(path)

    return Vfile.find
      .where.eq("owner_id", user.id)
      .eq("path", path)
      .findRowCount() == 1
  }

  def isDir(user: User, path: String): Boolean = {
    Argument.notNull(user)
    Argument.notEmpty(path)

    return Vfile.find
      .where
      .eq("owner_id", user.id)
      .eq("path", path)
      .eq("type", Vfile.Filetype.DIR)
      .findRowCount() == 1
  }

  def isEmptyDir(user: User, path: String): Boolean = {
    Argument.notNull(user)
    Argument.notEmpty(path)

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
    Argument.notEmpty(path)

    return Vfile.find
      .where
      .eq("owner_id", user.id)
      .eq("path", path)
      .eq("type", Vfile.Filetype.FILE)
      .findRowCount() == 1
  }

  def readFile(user: User, path: String): Vfile = {
    Argument.notNull(user)
    Argument.notEmpty(path)

    val p =
      if (path.endsWith("/")) path.substring(0, path.length - 1)
      else path

    return Vfile.find
      .where
      .eq("owner_id", user.id)
      .eq("path", p)
      .findUnique()
  }

  @Transactional def deleteFile(user: User, path: String): Boolean = {
    Argument.notNull(user)
    Argument.notEmpty(path)

    def delete(file: Vfile): Unit =
      if (file.isDir)
        for (child <- lsDir(user, file.getPath))
          delete(child)
      else if (file.isDir)
        file.delete()
      else
        throw new IllegalStateException(s"Deleting non existing file '$path' ")

    val f: Vfile = readFile(user, path)
    if (f != null) {
      delete(f)

      return true
    }
    else {
      Logger.warn("Vfile path to delete does not exist:: " + path)

      return false
    }
  }

  @Transactional def moveFile(user: User, path: String, newPath: String): Boolean = {
    Argument.notNull(user)
    Argument.notEmpty(path)
    Argument.notEmpty(newPath)

    Argument.require(fileExists(user, path), s"Source path $path must exist to move file")
    Argument.requireNot(fileExists(user, newPath), s"Target path $newPath must not exist to move file")

    val f = readFile(user, path)
    val children =
      if (f.isDir) lsDir(user, path)
      else Collections.emptyList[Vfile]

    f.setPath(newPath)
    f.update

    Logger.info(s"File moved from '$path' to '$newPath'")

    children.map { child =>
      moveFile(user, child.getPath, child.getPath.replaceAll(s"^$path", newPath))
    }.fold(true)(_ && _)
  }
}
