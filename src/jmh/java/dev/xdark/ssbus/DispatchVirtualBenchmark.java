package dev.xdark.ssbus;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import dev.xdark.ssbus.*;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class DispatchVirtualBenchmark {
  private final int NUM_CONSUMERS = 200;
  private Bus<Event> bus;
  private Event event;

  @Setup
  public void setup() {
    bus = new Bus<>(Event.class);
    for (int i = 0; i < NUM_CONSUMERS; i++) {
      bus.register(this);
    }
    event = new Event(null);
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  @Fork(value = 2)
  @Warmup(iterations = 2)
  @Measurement(iterations = 3)
  @OperationsPerInvocation(NUM_CONSUMERS)
  public void dispatch(Blackhole hole) {
    event.hole = hole;
    bus.unsafeFireAndForget(event);
  }

  @Listener
  public void accept(Event e) {
    e.hole.consume(e);
  }

  public static final class Event {
    Blackhole hole;

    public Event(Blackhole hole) {
      this.hole = hole;
    }
  }
}
