package dev.xdark.ssbus;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import dev.xdark.ssbus.*;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class RegisterBenchmark {
  private final int NUM_CONSUMERS = 200;
  private ListenerCon listener;

  @Setup
  public void setup() {
    listener = new ListenerCon();
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  @Fork(value = 2)
  @Warmup(iterations = 2)
  @Measurement(iterations = 3)
  @OperationsPerInvocation(NUM_CONSUMERS)
  public void registerConsumer(Blackhole hole) {
    Bus<Event> bus = new Bus<>(Event.class);
    for (int i = 0; i < NUM_CONSUMERS; i++) {
      bus.register(listener);
    }
    hole.consume(bus);
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  @Fork(value = 2)
  @Warmup(iterations = 2)
  @Measurement(iterations = 3)
  @OperationsPerInvocation(NUM_CONSUMERS)
  public void registerStatic(Blackhole hole) {
    Bus<Event> bus = new Bus<>(Event.class);
    for (int i = 0; i < NUM_CONSUMERS; i++) {
      bus.register(RegisterBenchmark.class);
    }
    hole.consume(bus);
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  @Fork(value = 2)
  @Warmup(iterations = 2)
  @Measurement(iterations = 3)
  @OperationsPerInvocation(NUM_CONSUMERS)
  public void registerVirtual(Blackhole hole) {
    Bus<Event> bus = new Bus<>(Event.class);
    for (int i = 0; i < NUM_CONSUMERS; i++) {
      bus.register(this);
    }
    hole.consume(bus);
  }

  @Listener
  public void acceptVirtual(Event e) {
    e.hole.consume(e);
  }

  @Listener
  public static void acceptStatic(Event e) {
    e.hole.consume(e);
  }

  public static final class ListenerCon implements Consumer<Event> {
    public void accept(Event e) {
      e.hole.consume(e);
    }
  }

  public static final class Event {
    Blackhole hole;

    public Event(Blackhole hole) {
      this.hole = hole;
    }
  }
}
