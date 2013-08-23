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
package com.sics.sicsthsense.auth.openid;


import com.google.common.base.Objects;
import com.sics.sicsthsense.model.security.Authority;

import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <p>Value object to provide the following to {@link OpenIDAuthenticator}:</p>
 * <ul>
 * <li>Storage of the necessary credentials for OpenID authentication</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class OpenIDCredentials {

  private final UUID sessionToken;
  private final Set<Authority> requiredAuthorities;

  /**
   * @param sessionToken        The session token acting as a surrogate for the OpenID token
   * @param requiredAuthorities The authorities required to authenticate (provided by the {@link uk.co.froot.demo.openid.auth.annotation.RestrictedTo} annotation)
   */
  public OpenIDCredentials(
    UUID sessionToken,
    Set<Authority> requiredAuthorities) {
    this.sessionToken = checkNotNull(sessionToken);
    this.requiredAuthorities = checkNotNull(requiredAuthorities);
  }

  /**
   * @return The OpenID token
   */
  public UUID getSessionToken() {
    return sessionToken;
  }

  /**
   * @return The authorities required to successfully authenticate
   */
  public Set<Authority> getRequiredAuthorities() {
    return requiredAuthorities;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    final OpenIDCredentials that = (OpenIDCredentials) obj;

    return sessionToken.equals(that.sessionToken);
  }

  @Override
  public int hashCode() {
    return (31 * sessionToken.hashCode());
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("sessionId", sessionToken)
      .add("authorities", requiredAuthorities)
      .toString();
  }

}
