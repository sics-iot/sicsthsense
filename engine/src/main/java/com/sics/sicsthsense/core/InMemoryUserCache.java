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
package com.sics.sicsthsense.core;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.sics.sicsthsense.core.User;

/**
 * <p>Cache to provide the following to {@link User} authenticators:</p>
 * <ul>
 * <li>In-memory thread-safe cache for client user instances</li>
 * <li>Provision of fast lookup for request authentication</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public enum InMemoryUserCache {

  // Provide a global singleton for the application
  INSTANCE;

  // A lot of threads will hit this cache
  private volatile Cache<UUID, User> userCache;

  InMemoryUserCache() {
    reset(15, TimeUnit.MINUTES);
  }

  /**
   * Resets the cache and allows the expiry time to be set (perhaps for testing)
   *
   * @param duration The duration before a user must manually authenticate through a web form due to inactivity
   * @param unit     The {@link java.util.concurrent.TimeUnit} that duration is expressed in
   */
  public InMemoryUserCache reset(int duration, TimeUnit unit) {

    // Build the cache
    if (userCache != null) {
      userCache.invalidateAll();
    }

    // If there is no activity against a key then we want
    // it to be expired from the cache, but each fresh write
    // will reset the expiry timer
    userCache = CacheBuilder
      .newBuilder()
      .expireAfterWrite(duration, unit)
      .maximumSize(1000)
      .build();

    return INSTANCE;
  }

  /**
   * @param sessionToken The session token to locate the user (not JSESSIONID)
   *
   * @return The matching User or absent
   */
  public Optional<User> getBySessionToken(UUID sessionToken) {

    // Check the cache
    Optional<User> userOptional = Optional.fromNullable(userCache.getIfPresent(sessionToken));
    if (userOptional.isPresent()) {
      // Ensure we refresh the cache on a check to maintain the session timeout
      userCache.put(sessionToken, userOptional.get());
    }
    return userOptional;
  }

  /**
   * @param sessionToken The session token to use to locate the user
   * @param user      The User to cache
   */
  public void put(UUID sessionToken, User user) {
    Preconditions.checkNotNull(user);
    userCache.put(sessionToken, user);
  }

  public void hardDelete(User user) {
    Preconditions.checkNotNull(user);
		//if (!user.hasSessionToken()) { return; }
    Preconditions.checkNotNull(user.getSessionToken());
    userCache.invalidate(user.getSessionToken());
  }

  public Optional<User> getByOpenIDIdentifier(String openIDIdentifier) {

    Map<UUID, User> map = userCache.asMap();

    for (Map.Entry<UUID, User> entry : map.entrySet()) {
      if (entry.getValue().getOpenIDIdentifier().equals(openIDIdentifier)) {
        return Optional.of(entry.getValue());
      }
    }
    return Optional.absent();
  }

  public Optional<User> getByEmail(String email) {
    Map<UUID, User> map = userCache.asMap();

    for (Map.Entry<UUID, User> entry : map.entrySet()) {
      if (entry.getValue().getEmail().equals(email)) {
        return Optional.of(entry.getValue());
      }
    }

    return Optional.absent();
  }
}
