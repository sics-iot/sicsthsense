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
package se.sics.sicsthsense.model.openid;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * <p>Memento to provide the following to OpenID authentication web handling:</p>
 * <ul>
 * <li>Persistence store of the discovery information</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class DiscoveryInformationMemento {

  /**
   * The OP endpoint URL.
   */
  @JsonProperty
  String opEndpoint;

  /**
   * The claimed identifier, i.e. the user's identity key.
   */
  @JsonProperty
  String claimedIdentifier;

  /**
   * The delegate, or OP-Local identifier.
   * The key through which the OP remembers the user's account.
   */
  @JsonProperty
  String delegate;

  /**
   * The OpenID protocol version, or target service type discovered through Yadis.
   */
  @JsonProperty
  String version;

  /**
   * All service types discovered for the endpoint.
   */
  @JsonProperty
  Set<String> types = Sets.newLinkedHashSet();

  public String getOpEndpoint() {
    return opEndpoint;
  }

  public void setOpEndpoint(String opEndpoint) {
    this.opEndpoint = opEndpoint;
  }

  public String getClaimedIdentifier() {
    return claimedIdentifier;
  }

  public void setClaimedIdentifier(String claimedIdentifier) {
    this.claimedIdentifier = claimedIdentifier;
  }

  public String getDelegate() {
    return delegate;
  }

  public void setDelegate(String delegate) {
    this.delegate = delegate;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public Set<String> getTypes() {
    return types;
  }

  public void setTypes(Set<String> types) {
    this.types = types;
  }
}
