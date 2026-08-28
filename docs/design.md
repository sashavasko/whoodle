## Source and Sink Semantics

A `Source` or `Sink` represents a reusable, configured endpoint. It does not represent an active read or write operation.

The schema associated with an endpoint describes the logical data that Whoodle expects to cross that endpoint. It does not necessarily describe the complete physical schema of the underlying storage system.

```java
public interface Source<T> {
    Schema schema();
}

public interface Sink<T> {
    Schema schema();
}
```

### Source Schema

For a `Source`, the schema defines the requested logical view of the underlying data. It may specify:

* fields to read
* field ordering
* required and optional fields
* target Java types
* nested structures
* conversions from physical values
* projections that may be pushed down to the backend

The source implementation translates the requested Whoodle schema into the operations supported by the backend. Any projection or conversion that cannot be performed by the backend is completed within Whoodle.

```text
Physical source representation
        ↓
Backend projection and conversion
        ↓
Requested Whoodle schema
        ↓
Java object
```

The source schema is therefore a projection contract, not merely a description of all data available from the source.

### Sink Schema

For a `Sink`, the schema defines the requested logical output representation. It may specify:

* fields to write
* output field names and ordering
* required and optional fields
* nested output structures
* extraction of values from Java objects
* conversions to the destination representation

```text
Java object
        ↓
Whoodle extraction and conversion
        ↓
Requested output schema
        ↓
Physical destination representation
```

The sink implementation translates the logical output schema into the physical representation required by the destination.

### Physical Schema Discovery

Discovery of a backend’s physical schema is separate from the logical schema associated with a `Source` or `Sink`.

If supported, schema discovery should be represented as an optional backend capability rather than changing the meaning of `schema()`:

```java
public interface SchemaDiscoverable {
    Schema discoverSchema() throws Exception;
}
```

The exact discovery API remains to be determined.

## Access Capabilities

`Stream<T>` and `Flow.Publisher<T>` are parallel, first-class access models. A source may support either or both:

```java
public interface StreamSource<T> extends Source<T> {
    Stream<T> stream() throws Exception;
}

public interface FlowSource<T> extends Source<T> {
    Flow.Publisher<T> publisher();
}
```

A file-oriented source will commonly implement `StreamSource<T>`, while an event-oriented source will commonly implement `FlowSource<T>`. Sources such as JDBC or Arrow Flight may support both.

The corresponding sink capabilities are provisionally defined as:

```java
public interface StreamSink<T> extends Sink<T> {
    Writer<T> openWriter() throws Exception;
}

public interface FlowSink<T> extends Sink<T> {
    Flow.Subscriber<T> subscriber();
}
```

An opened writer represents one active synchronous write operation:

```java
public interface Writer<T> extends AutoCloseable {
    void write(T value) throws Exception;

    default void writeAll(Stream<? extends T> values)
            throws Exception {
        Iterator<? extends T> iterator = values.iterator();

        while (iterator.hasNext()) {
            write(iterator.next());
        }
    }

    @Override
    void close() throws Exception;
}
```

This writer abstraction is provisional. Further design work is needed to determine whether an explicit writer provides sufficient value or whether a sink should consume streams directly.

Regardless of the final API, operational state and resource ownership belong to the active read or write operation rather than to the reusable `Source` or `Sink` endpoint.


## Stream and Flow as First-Class Data Access Models

Whoodle supports two parallel data access models: `Stream<T>` and `Flow.Publisher<T>`. Neither model is treated as a universal replacement for the other. Each preserves the execution semantics native to the underlying data source.

* `Stream<T>` provides synchronous, pull-based access to data.
* `Flow.Publisher<T>` provides asynchronous, demand-driven access with backpressure.

File-oriented and finite sources such as JSON, Parquet, Avro, and Vortex naturally expose data through `Stream<T>`. Event-oriented sources such as Kafka and RabbitMQ naturally expose data through `Flow.Publisher<T>`. Sources such as JDBC and Arrow Flight may support both models.

Capabilities are represented explicitly:

```java
interface StreamSource<T> extends Source<T> {
    Stream<T> stream();
}

interface FlowSource<T> extends Source<T> {
    Flow.Publisher<T> publisher();
}
```

A source may implement either or both interfaces. This makes its supported access models discoverable without relying on runtime exceptions.

Both models share the same underlying Whoodle schema, object conversion, and record-processing infrastructure. Whoodle unifies the representation and conversion of data while preserving the source’s natural execution model.

### Lifecycle and Resource Ownership

Each access model has its own resource lifecycle:

* Closing a `Stream<T>` closes its associated cursor and source resources.
* Cancelling a `Flow.Subscription` releases resources held for that subscription.
* `Flow.Subscriber.onComplete()` indicates natural completion.
* `Flow.Subscriber.onError()` reports failure and terminates the subscription.
* A publisher must not emit more items than the subscriber has requested.

### Model Conversion

Whoodle may provide explicit adapters between the two models:

```java
Flow.Publisher<T> publish(
        Stream<T> stream,
        Executor executor);

Stream<T> blockingStream(
        Flow.Publisher<T> publisher);
```

These conversions are not transparent. Their names must communicate the semantic change.

Converting a stream to a publisher introduces asynchronous execution, subscription demand, cancellation, and executor ownership. Converting a publisher to a stream introduces blocking, buffering, termination, and interruption concerns.

Adapters provide interoperability where necessary, but Whoodle does not force one access model through the other internally.
