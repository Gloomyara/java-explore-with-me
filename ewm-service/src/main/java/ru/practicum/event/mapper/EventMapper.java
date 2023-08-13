package ru.practicum.event.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.model.Event;
import ru.practicum.user.mapper.UserMapper;

import java.util.List;

@Mapper(uses = {CategoryMapper.class, UserMapper.class})
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    Event toEntity(NewEventDto newEventDto);

    EventFullDto toDto(Event event);

    List<EventFullDto> toDto(List<Event> event);

    List<EventShortDto> toShortDto(List<Event> event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category.id", source = "categoryId")
    void update(EventUpdateDto dto, @MappingTarget Event entity);

}
