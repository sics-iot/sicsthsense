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

/* Description:
 * TODO:
 * */

package models;

import play.Logger;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "functions")
//@Inheritance
//@DiscriminatorValue("operators")
public class Function extends Model {
    @Id
    public Long id;

    @ManyToOne(cascade = {CascadeType.ALL})
    public User owner;

    @ManyToMany(cascade = {CascadeType.ALL})
    public List<Stream> outputStreams;

    @ManyToMany(cascade = {CascadeType.ALL})
    public List<Stream> inputStreams;

    public Function() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Function(User user, String output, String inputStream1, String inputStream2) {
        this.owner = user;
        Logger.warn("Operator() " + output + " " + inputStream1 + " " + inputStream2);
    }

    public static Function create(User user, String output, String inputStream1, String inputStream2) {
        Logger.warn("Function.create() " + output + " " + inputStream1 + " " + inputStream2);
        Function function = new Function(user, output, inputStream1, inputStream2);
        try {
            function.save();
        } catch (Exception e) {
        }
        return function;
    }

    /**
     *
     */
    private static final long serialVersionUID = 5004038592549122787L;

    public static Model.Finder<Long, Function> find = new Model.Finder<Long, Function>(Long.class, Function.class);
}
