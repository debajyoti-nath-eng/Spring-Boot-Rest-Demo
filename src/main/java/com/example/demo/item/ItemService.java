package com.example.demo.item;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> findAll() {
        return itemRepository.findAll(Sort.by("id")).stream()
                .map(ItemEntity::toItem)
                .toList();
    }

    public Item findById(long id) {
        return getEntity(id).toItem();
    }

    @Transactional
    public Item create(ItemRequest request) {
        ItemEntity entity = new ItemEntity(request.name(), request.description());
        return itemRepository.save(entity).toItem();
    }

    @Transactional
    public Item update(long id, ItemRequest request) {
        ItemEntity entity = getEntity(id);
        entity.setName(request.name());
        entity.setDescription(request.description());
        return itemRepository.save(entity).toItem();
    }

    @Transactional
    public void delete(long id) {
        itemRepository.delete(getEntity(id));
    }

    private ItemEntity getEntity(long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
    }
}
