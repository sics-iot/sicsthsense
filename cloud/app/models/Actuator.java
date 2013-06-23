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

package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.ebean.Model;

@Entity
@Table(name = "actuators")
public class Actuator extends Model {
	/**
	 * The serialization runtime associates with each serializable class a version
	 * number, called a serialVersionUID
	 */
	// @Transient
	private static final long serialVersionUID = 6496834518631996535L;
	@Id
	public Long id;

	@ManyToOne
	public User owner;

	// @OneToMany(mappedBy="resource", cascade=CascadeType.ALL)
	// public List<Stream> outputStreams;

	/** HTML, JSON, RegEx */
	private String inputParser;

	public static Model.Finder<Long, Actuator> find = new Model.Finder<Long, Actuator>(
			Long.class, Actuator.class);

	public Actuator(User user) {
		super();
		this.owner = user;
	}

	public Actuator() {
		super();
	}

	public static Actuator create(User user) {
		if (user != null) {
			Actuator act = new Actuator(user);
			act.save();
			return act;
		}
		return null;
	}

	public void setInputParser(String inputParser) {
		this.inputParser = inputParser;
		if (this.id != 0) {
			this.update();
		}
	}

}
