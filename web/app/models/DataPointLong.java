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

import javax.persistence.*;

//@Entity
public class DataPointLong extends DataPoint {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6502881310122879601L;
	
	public Long data;

	@Column(nullable = false)
	@ManyToOne
	public Stream stream;
	public DataPointLong() {
		this(null, null, null);
		// TODO Auto-generated constructor stub
	}
	
	public DataPointLong(Stream stream, Long data, Long timestamp) {
		this.stream = stream;
		this.data = data;
		this.timestamp = timestamp;
	}

	public DataPointLong add() {
		// DataPointDouble dataPoint = new DataPoint(stream, data, timestamp);
		if (stream != null) {
			this.save();
			return this;
		}
		return null;
	}
	
	public Long getData() {
		return data;
  }

	@Override
	public String toTSV() {
		return timestamp +"\t"+ data;
	}
}
