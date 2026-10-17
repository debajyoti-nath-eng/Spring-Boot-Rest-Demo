package com.example.demo.item;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ItemService {

    private final ConcurrentMap<Long, Item> items = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong();

    public List<Item> findAll() {
        return items.values().stream()
                .sorted(Comparator.comparingLong(Item::id))
                .toList();
    }

    public Item findById(long id) {
        Item item = items.get(id);
        if (item == null) {
            throw new ItemNotFoundException(id);
        }
        return item;
    }

    public Item create(ItemRequest request) {
        long id = nextId.incrementAndGet();
        Item item = new Item(id, request.name(), request.description());
        items.put(id, item);
        return item;
    }

    public Item update(long id, ItemRequest request) {
        return items.compute(id, (key, existing) -> {
            if (existing == null) {
                throw new ItemNotFoundException(id);
            }
            return new Item(id, request.name(), request.description());
        });
    }

    public void delete(long id) {
        if (items.remove(id) == null) {
            throw new ItemNotFoundException(id);
        }
    }
}
