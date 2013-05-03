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
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

/* Description:
 * TODO:
 * */

package models;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;

/** T could be any comparable type; i.e. Long, Double, String, etc. */
@javax.persistence.MappedSuperclass
public abstract class DataPoint extends Model implements Comparable<DataPoint> {

	@Column(name = "stream_id", nullable = false)
	@ManyToOne
	public Stream stream;

	@Id
	public Long id;
	
	/**
	 */
	private static final long serialVersionUID = 2919758328697338009L;

	/** T could be any comparable type; i.e. Long, Double, String, etc. */
	//@Column(unique = true, nullable = false)
	//@Id
	public Long timestamp;
	
	public abstract DataPoint add();
	public abstract Object getData();
	public abstract String toTSV();

	public DataPoint() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int compareTo(DataPoint point) {
		return Long.valueOf(this.timestamp).compareTo(point.timestamp);
	}

//	public abstract long getCount();
//
//
//	public abstract List<? extends DataPoint> getByStream(Stream stream);
//
//	public abstract List<? extends DataPoint> getByStreamTail(Stream stream, long tail);
//
//	public abstract List<? extends DataPoint> getByStreamLast(Stream stream, long last);
//
//	public abstract List<? extends DataPoint> getByStreamSince(Stream stream, long since);
//
//	public abstract void deleteByStream(Stream stream);

}
