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

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.openid4java.consumer.ConsumerManager;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>In-memory cache to provide the following to OpenID authentication:</p>
 * <ul>
 * <li>Short term storage of thread local session data</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public enum InMemoryOpenIDCache {

  INSTANCE;

  /**
   * Simple cache for {@link org.openid4java.consumer.ConsumerManager} entries
   */
  private final Cache<UUID, ConsumerManager> consumerManagerCache = CacheBuilder.newBuilder()
    .maximumSize(1000)
    .expireAfterWrite(2, TimeUnit.MINUTES)
    .build();

  /**
   * <h3>Note that this is not horizontally scalable</h3>
   *
   * @param sessionToken The session token
   *
   * @return The mapped OpenID {@link org.openid4java.consumer.ConsumerManager} created for that session token
   */
  public Optional<ConsumerManager> getConsumerManager(UUID sessionToken) {

    return Optional.fromNullable(consumerManagerCache.getIfPresent(sessionToken));

  }

  /**
   * <h3>Note that this is not horizontally scalable</h3>
   *
   * @param sessionToken    The session token
   * @param consumerManager The OpenID ConsumerManager for this UUID
   */
  public void putConsumerManager(UUID sessionToken, ConsumerManager consumerManager) {

    consumerManagerCache.put(sessionToken, consumerManager);

  }

}
