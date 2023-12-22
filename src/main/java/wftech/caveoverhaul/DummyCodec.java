package wftech.caveoverhaul;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;

public class DummyCodec<A> implements Codec<A> {

	@Override
	public DataResult encode(Object input, DynamicOps ops, Object prefix) {
		return null;
	}

	@Override
	public DataResult decode(DynamicOps ops, Object input) {
		return null;
	}
	
	@Override
	public MapCodec<A> fieldOf(final String name) {
		return new DummyMapCodec();
	}
	
	@Override
	public <S> Codec<S> xmap(final Function<? super A, ? extends S> to, final Function<? super S, ? extends A> from) {
		return null;
	}
}
