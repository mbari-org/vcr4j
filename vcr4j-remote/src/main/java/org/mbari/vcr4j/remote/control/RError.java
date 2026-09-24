/*
 * Copyright © 2008 MBARI (brian@mbari.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mbari.vcr4j.remote.control;

import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoError;

import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class RError implements VideoError {

    private final boolean connectionError;
    private final boolean parserError;
    private final boolean unknownError;
    private final VideoCommand<?> videoCommand;

    private final String message;

    private final Throwable exception;

    public RError(boolean connectionError,
                  boolean parserError,
                  boolean unknownError,
                  VideoCommand<?> videoCommand,
                  String message,
                  Throwable throwable) {
        this.connectionError = connectionError;
        this.parserError = parserError;
        this.unknownError = unknownError;
        this.videoCommand = videoCommand;
        this.message = message;
        this.exception = throwable;
    }

    public RError(boolean connectionError, boolean parserError, boolean unknownError, VideoCommand<?> videoCommand) {
        this(connectionError, parserError, unknownError, videoCommand, null, null);
    }

    public RError(boolean connectionError, boolean parserError, boolean unknownError) {
        this(connectionError, parserError, unknownError, null, null, null);
    }

    @Override
    public boolean hasError() {
        return connectionError || parserError ||unknownError;
    }

    @Override
    public Optional<VideoCommand<?>> getVideoCommand() {
        return Optional.ofNullable(videoCommand);
    }

    public Optional<Throwable> getException() {
        return Optional.ofNullable(exception);
    }

    public boolean isConnectionError() {
        return connectionError;
    }

    public boolean isParseError() {
        return  parserError;
    }

    public boolean isUnknownError() {
        return unknownError;
    }

    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }

    @Override
    public String toString() {
        return "RError{" +
                "connectionError=" + connectionError +
                ", parserError=" + parserError +
                ", unknownError=" + unknownError +
                ", videoCommand=" + videoCommand +
                ", message='" + message + '\'' +
                ", exception=" + exception +
                '}';
    }
}
