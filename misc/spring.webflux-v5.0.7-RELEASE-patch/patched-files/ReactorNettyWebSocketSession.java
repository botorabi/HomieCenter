/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package org.springframework.web.reactive.socket.adapter;

import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;
import reactor.netty.NettyPipeline;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;

import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;


/**
 * {@link WebSocketSession} implementation for use with the Reactor Netty's
 * {@link NettyInbound} and {@link NettyOutbound}.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public class ReactorNettyWebSocketSession
		extends NettyWebSocketSessionSupport<ReactorNettyWebSocketSession.WebSocketConnection> {

	/**
	 * NOTE (boto): Increase the default WebSocket packet size, it was previously 64 kBytes.
	 *              For streaming multimedia it was too low.
	 */
	private int frameMaxSize = 5 * 1024 * 1024;

	public ReactorNettyWebSocketSession(WebsocketInbound inbound, WebsocketOutbound outbound,
			HandshakeInfo info, NettyDataBufferFactory bufferFactory) {

		super(new WebSocketConnection(inbound, outbound), info, bufferFactory);
	}


	@Override
	public Flux<WebSocketMessage> receive() {
		return getDelegate().getInbound()
				.aggregateFrames(getFrameMaxSize())
				.receiveFrames()
				.map(super::toMessage);
	}

	public int getFrameMaxSize() {
		return frameMaxSize > 0 ? frameMaxSize : DEFAULT_FRAME_MAX_SIZE;
	}

	public void setFrameMaxSize(int frameMaxSize) {
		this.frameMaxSize = frameMaxSize;
	}

	@Override
	public Mono<Void> send(Publisher<WebSocketMessage> messages) {
		Flux<WebSocketFrame> frames = Flux.from(messages).map(this::toFrame);
		return getDelegate().getOutbound()
				.options(NettyPipeline.SendOptions::flushOnEach)
				.sendObject(frames)
				.then();
	}

	@Override
	public Mono<Void> close(CloseStatus status) {
		WebSocketFrame closeFrame = new CloseWebSocketFrame(status.getCode(), status.getReason());
		return getDelegate().getOutbound().sendObject(closeFrame).then();
	}


	/**
	 * Simple container for {@link NettyInbound} and {@link NettyOutbound}.
	 */
	public static class WebSocketConnection {

		private final WebsocketInbound inbound;

		private final WebsocketOutbound outbound;


		public WebSocketConnection(WebsocketInbound inbound, WebsocketOutbound outbound) {
			this.inbound = inbound;
			this.outbound = outbound;
		}

		public WebsocketInbound getInbound() {
			return this.inbound;
		}

		public WebsocketOutbound getOutbound() {
			return this.outbound;
		}
	}

}
