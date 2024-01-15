package wftech.caveoverhaul;

import java.util.function.Function;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

public class DummyMapCodec<A> extends MapCodec<A> {

	public DummyMapCodec() {}
	
	@Override
	public <T> DataResult<A> decode(DynamicOps<T> ops, MapLike<T> input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> RecordBuilder<T> encode(A input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Stream<T> keys(DynamicOps<T> ops) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S> MapCodec<S> xmap(final Function<? super A, ? extends S> to, final Function<? super S, ? extends A> from) {
		return new DummyMapCodec();
	}
	
	@Override
	public Codec<A> codec() {
		return null;
	}
}