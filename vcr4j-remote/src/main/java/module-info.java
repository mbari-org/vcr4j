module vcr4j.remote {
  
  requires com.google.gson;
  requires io.reactivex.rxjava3;
  requires vcr4j.core;
  
  exports org.mbari.vcr4j.remote.control.commands.localization;
  exports org.mbari.vcr4j.remote.control.commands;
  exports org.mbari.vcr4j.remote.control;
  exports org.mbari.vcr4j.remote.player;

  opens org.mbari.vcr4j.remote.player to com.google.gson;
  opens org.mbari.vcr4j.remote.control.commands to com.google.gson;
  opens org.mbari.vcr4j.remote.control.commands.localization to com.google.gson;
}
