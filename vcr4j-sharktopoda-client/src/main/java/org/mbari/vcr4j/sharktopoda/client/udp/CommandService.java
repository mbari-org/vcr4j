package org.mbari.vcr4j.sharktopoda.client.udp;

import com.google.gson.Gson;

import io.reactivex.subjects.Subject;

import org.mbari.vcr4j.sharktopoda.client.ClientController;
import org.mbari.vcr4j.sharktopoda.client.model.GenericCommand;
import org.mbari.vcr4j.sharktopoda.client.model.GenericResponse;
import org.mbari.vcr4j.sharktopoda.client.model.Video;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * @author Brian Schlining
 * @since 2017-12-05T13:54:00
 */
class CommandService {

    private final ClientController clientController;
    private final Subject<GenericResponse> responseSubject;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Gson gson = UdpIO.newGson();
    private final FramecaptureUdpIO framecaptureIO;

    public CommandService(ClientController clientController,
                          Subject<GenericCommand> commandSubject,
                          Subject<GenericResponse> responseSubject) {
        this.clientController = clientController;
        this.responseSubject = responseSubject;
        this.framecaptureIO = new FramecaptureUdpIO(this, commandSubject);
        commandSubject.subscribe(this::handleCommand);
    }


    public Gson getGson() {
        return gson;
    }

    private void handleCommand(GenericCommand cmd) {
        String c = cmd.getCommand().toLowerCase();
        switch (c) {
            case "connect":
                // Handled in FramecaptureUdpIO
                break;
            case "open":
                doOpen(cmd);
                break;
            case "close":
                doClose(cmd);
                break;
            case "show":
                doShow(cmd);
                break;
            case "request video information":
                doRequestVideoInfo(cmd);
                break;
            case "request all information":
                doRequestAllVideoInfos(cmd);
                break;
            case "play":
                doPlay(cmd);
                break;
            case "pause":
                doPause(cmd);
                break;
            case "request elapsed time":
                doRequestElapsedTime(cmd);
                break;
            case "request status":
                doRequestStatus(cmd);
                break;
            case "seek elapsed time":
                doSeekElapsedTime(cmd);
                break;
            case "framecapture":
                // Do nothing. It's handled on a different port by FramecaptureUdpIO
                break;
            case "frame advance":
                doFrameAdvance(cmd);
                break;
            default:

        }
    }

    private void doOpen(GenericCommand cmd) {
        GenericResponse r = new GenericResponse(cmd);
        if (cmd.getUrl() != null && cmd.getUuid() != null) {
            boolean status  = clientController.open(cmd.getUuid(), cmd.getUrl());
            String statusMessage = status ? "ok" : "failed";
            r.setStatus(statusMessage);
        } else {
            log.warn("Bad command: {}", gson.toJson(cmd));
            r.setStatus("failed");
        }
        responseSubject.onNext(r);
    }

    private void doClose(GenericCommand cmd) {
        GenericResponse r = new GenericResponse(); // Do nothing response
        if (cmd.getUuid() != null) {
            clientController.close(cmd.getUuid());
        }
        responseSubject.onNext(r);
    }

    private void doShow(GenericCommand cmd) {
        GenericResponse r = new GenericResponse();
        if (cmd.getUuid() != null) {
            clientController.show(cmd.getUuid());
        }
        responseSubject.onNext(r);
    }

    private void doRequestVideoInfo(GenericCommand cmd) {
        GenericResponse r = new GenericResponse(cmd);
        Optional<Video> opt = clientController.requestVideoInfo();

        opt.ifPresent(e -> {
            r.setUuid(e.getUuid());
            r.setUrl(e.getUrl());
            responseSubject.onNext(r);
        });
    }

    private void doRequestAllVideoInfos(GenericCommand cmd) {
        GenericResponse r = new GenericResponse(cmd);
        Video[] videos = clientController.requestAllVideoInfos()
                .stream()
                .toArray(Video[]::new);
        r.setVideos(videos);
        responseSubject.onNext(r);
    }

    private void doPlay(GenericCommand cmd) {
        doAction(cmd, (genericResponse) -> {
            double rate = cmd.getRate() == null ? 1.0 : cmd.getRate();
            boolean ok = clientController.play(cmd.getUuid(), rate);
            return ok ? "ok" : "failed";
        });
    }

    private void doPause(GenericCommand cmd) {
        doAction(cmd, genericResponse -> {
            boolean ok = clientController.pause(cmd.getUuid());
            return ok ? "ok" : "failed";
        });
    }

    private void doRequestElapsedTime(GenericCommand cmd) {
        doAction(cmd, (genericResponse) -> {
            Optional<Duration> opt = clientController.requestElapsedTime(cmd.getUuid());
            opt.ifPresent(genericResponse::setElapsedTime);
            return opt.isPresent() ? null : "failed";
        });
    }

    private void doRequestStatus(GenericCommand cmd) {
        doAction(cmd, genericResponse -> {
            Optional<Double> opt = clientController.requestRate(cmd.getUuid());
            String msg = "failed";
            if (opt.isPresent()) {
                double rate = opt.get();
                if (rate < 0.001 && rate > -0.001) {
                    msg = "paused";
                }
                else if (rate - 1.0 < 0.01) {
                    msg = "playing";
                } else if (rate > 0) {
                    msg = "shuttling forward";
                } else {
                    msg = "shuttling reverse";
                }
            }
            return msg;
        });
    }

    private void doSeekElapsedTime(GenericCommand cmd) {
        doAction(cmd, genericResponse -> {
            if (cmd.getElapsedTime() != null) {
                boolean ok = clientController.seekElapsedTime(cmd.getUuid(), cmd.getElapsedTime());
                genericResponse.setResponse(null); // No response is expected on success
                return ok ? "ok" : "failed";
            }
            else {
                return "failed";
            }
        });
    }

    private void doFrameAdvance(GenericCommand cmd) {
        doAction(cmd, genericResponse -> {
            boolean ok = clientController.frameAdvance(cmd.getUuid());
            genericResponse.setResponse(null); // No response is expected
            return ok ? "ok" : "failed";
        });
    }

    private void doAction(GenericCommand cmd, Function<GenericResponse, String> fn) {
        GenericResponse r = new GenericResponse(cmd);
        r.setStatus("failed");
        r.setUuid(r.getUuid());
        if (cmd.getUuid() != null) {
            try {
                String status = fn.apply(r);
                r.setStatus(status);
            }
            catch (Exception e) {
                r.setStatus("failed");
                log.warn("Failed to execute " + gson.toJson(cmd), e);
            }
        }
        else {
            r.setStatus("failed");
        }
        responseSubject.onNext(r);
    }

    protected CompletableFuture<GenericResponse> doFramecapture(GenericCommand cmd) {
        CompletableFuture<GenericResponse> f = new CompletableFuture<>();
        GenericResponse r = new GenericResponse(cmd);
        r.setImageReferenceUuid(cmd.getImageReferenceUuid());
        r.setStatus("failed");
        if (cmd.getUuid() != null &&
                cmd.getImageReferenceUuid() != null &&
                cmd.getImageLocation() != null) {

            Path path = Paths.get(cmd.getImageLocation());
            clientController.framecapture(cmd.getUuid(), path)
                    .thenAccept(fc -> {
                        r.setImageLocation(fc.getSaveLocation().toString());
                        r.setElapsedTime(fc.getSnapTime());
                        r.setStatus("ok");
                     })
                    .thenAccept(v -> f.complete(r));

        }
        else {
            f.completeExceptionally(new RuntimeException("Missing parameters needed to acquire framecapture"));
        }
        return f;
    }




}
