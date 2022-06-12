package org.mbari.vcr4j.remote.player;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.mbari.vcr4j.remote.control.commands.RResponse;
import org.mbari.vcr4j.remote.control.commands.loc.*;


/**
 * This takes all the localization cmd/requests and dumps them into an
 * observable so the implementation can do whatever it needs to do to manage the
 * localizations. As implemented all localization requests response with an OK.
 */
public abstract class RxPlayer implements Player {

    private final Subject<LocalizationsCmd<?, ?>> localizationsCmdSubject;

    public RxPlayer() {
        PublishSubject<LocalizationsCmd<?, ?>> subject = PublishSubject.create();
        this.localizationsCmdSubject = subject.toSerialized();
    }

    public Observable<LocalizationsCmd<?, ?>> getLocalizationsCmdObservable() {
        return localizationsCmdSubject;
    }

    @Override
    public AddLocalizationsCmd.Response handleAddLocalizationsRequest(AddLocalizationsCmd.Request request) {
        localizationsCmdSubject.onNext(new AddLocalizationsCmd(request));
        return new AddLocalizationsCmd.Response(RResponse.OK);
    }

    @Override
    public RemoveLocalizationsCmd.Response handleDeleteLocalizationsRequest(RemoveLocalizationsCmd.Request request) {
        localizationsCmdSubject.onNext(new RemoveLocalizationsCmd(request));
        return new RemoveLocalizationsCmd.Response(RResponse.OK);
    }

    @Override
    public UpdateLocalizationsCmd.Response handleUpdateLocalizationsRequest(UpdateLocalizationsCmd.Request request) {
        localizationsCmdSubject.onNext(new UpdateLocalizationsCmd(request));
        return new UpdateLocalizationsCmd.Response(RResponse.OK);
    }

    @Override
    public ClearLocalizationsCmd.Response handleClearLocalizationsRequest(ClearLocalizationsCmd.Request request) {
        localizationsCmdSubject.onNext(new ClearLocalizationsCmd(request));
        return new ClearLocalizationsCmd.Response(RResponse.OK);
    }
}
