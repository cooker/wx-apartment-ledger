package com.wx.apartment.ledger.domain.house;

import java.util.List;
import java.util.Optional;

public interface HouseRepository {

    House save(House house);

    Optional<House> findById(Long id);

    List<House> findPage(String keyword, long offset, long pageSize);

    long countByKeyword(String keyword);

    void deleteById(Long id);
}

