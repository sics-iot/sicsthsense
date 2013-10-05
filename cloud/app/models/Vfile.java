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

 * Authors:
 *  26/08/2013 Adrian Kündig (adkuendi@ethz.ch)
 *  Before     Unknown
 * */

package models;

import com.avaje.ebean.annotation.EnumValue;
import logic.Argument;
import logic.StreamDrive;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;

@Entity
@Table(name = "vfiles", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"owner_id", "path"})
})
public class Vfile extends Model {

    private static final long serialVersionUID = 1766439519493690841L;

    /* should this be a UUID instead?*/
    @Id
    public Long id;

    @Constraints.Required
    @Column(nullable = false)
    String path;

    @Constraints.Required
    @ManyToOne(optional = false)
    User owner;

    public static enum Filetype {
        @EnumValue("F")
        FILE,
        @EnumValue("D")
        DIR
    }

    @Constraints.Required
    @Column(nullable = false)
    public Filetype type;

    @OneToOne(optional = true)
    Stream stream;

    public static Model.Finder<Long, Vfile> find = new Model.Finder<Long, Vfile>(Long.class, Vfile.class);

    public Vfile(String path, User owner, Filetype type, Stream stream) {
        super();
        this.path = path;
        this.owner = owner;
        this.type = type;
        this.stream = stream;
    }

    public Vfile() {
        super();
    }

    public Vfile(User user, String path, Filetype type) {
        this(path, user, type, null);
    }

    public static Vfile create(Vfile file) {
        Argument.notNull(file);
        Argument.notNull(file.owner);

        file.save();

        return file;
    }

    public Filetype getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isFile() {
        return type == Filetype.FILE;
    }

    public boolean isDir() {
        return type == Filetype.DIR;
    }

    public User getOwner() {
        return owner;
    }

    public String getName() {
        return StreamDrive.getName(path);
    }

    public String getParentPath() {
        return StreamDrive.getParentPath(path);
    }

    public void setLink(Stream linkedStream) {
        this.stream = linkedStream;
        if (id != 0) {
            this.save();
        }
    }

    public Stream getLink() {
        return stream;
    }

    //remove invalid characters
    public void verify() {
        this.path = path.replaceAll("[:\"*?<>|']+", "");
    }

    @Override
    public void update() {
        verify();
        super.update();
    }

    @Override
    public void save() {
        verify();
        super.save();
    }
}
