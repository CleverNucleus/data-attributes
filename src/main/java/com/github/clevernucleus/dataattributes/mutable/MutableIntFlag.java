package com.github.clevernucleus.dataattributes.mutable;

public interface MutableIntFlag {
	default void setUpdateFlag(int flag) {}
	default int getUpdateFlag() { return 0; }
}
