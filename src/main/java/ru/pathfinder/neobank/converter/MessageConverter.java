package ru.pathfinder.neobank.converter;

import ru.pathfinder.neobank.domain.CommandData;

public interface MessageConverter<T> {

    CommandData convert(T data);

}
