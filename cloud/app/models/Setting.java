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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;
import javax.persistence.Table;
import play.data.validation.Constraints;

import play.Logger;
import play.db.ebean.Model;

@Entity
@Table(name = "settings", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"name" })
})
public class Setting extends Model {

	private static final long serialVersionUID = 1766439519493690841L;

	@Id
	public Long id;

	@Column(nullable = false)
	@Constraints.Required
	public String name;

	@Constraints.Required
	public String val;

	public static Finder<Long,Setting> find = new Finder<Long,Setting>(Long.class, Setting.class);

	public Setting(String name, String val) {
	 this.name = name;
	 this.val  = val;
	}

	public void delete() {
		//TODO: Check dependencies
		//this.stream.file = null;
		super.delete();
	}

	public static Setting findName(String name) {
		Setting setting = Setting.find.where().eq("name",name).findUnique();
		if (setting==null) {
			setting = new Setting(name,"unset");
			setting.save();
		}
		return setting;
	}

	public static String getSetting(String name) {
		Setting setting = Setting.find.where().eq("name",name).findUnique();
		if (setting!=null) { return setting.val; }
		return null;
	}

	public static void setSetting(String name, String val) {
		Setting setting = Setting.find.where().eq("name",name).findUnique();
		Logger.info("setting: "+name+" set to "+val);
		if (setting==null) {
			setting = new Setting(name,val);
			setting.save();
		} else {
			setting.val = val;
		}
	}

}
