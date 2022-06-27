module vcr4j.remote {
  requires com.google.gson;
  requires io.reactivex.rxjava3;
  requires org.slf4j;
  requires vcr4j.core;
  exports org.mbari.vcr4j.remote.player;
  exports org.mbari.vcr4j.remote.control;
  exports org.mbari.vcr4j.remote.control.commands;
  exports org.mbari.vcr4j.remote.control.commands.loc;

  opens org.mbari.vcr4j.remote.control.commands to com.google.gson;
  opens org.mbari.vcr4j.remote.control.commands.loc to com.google.gson;
}
