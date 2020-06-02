package com.shinemo.stallup.common.statemachine;

import com.fasterxml.jackson.annotation.JsonValue;

public interface BaseEnum<E extends Enum<E>> {

	@JsonValue
	int getId();

	String getName();

}