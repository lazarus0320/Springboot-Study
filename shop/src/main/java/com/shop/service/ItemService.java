package com.shop.service;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImgDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        Item item = itemFormDto.createItem();
        itemRepository.save(item);

        for (int i = 0; i < itemImgFileList.size(); i++) {
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            if (i == 0) itemImg.setRepimgYn("Y");
            else itemImg.setRepimgYn("N");

            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }
        return item.getId();
    }

    // 상품의 디테일 요소 조회
    // item 엔티티와 img 정보 엔티티를 itemFormDto 객체로 변환 후 반환하는 조회 기능
    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId) {

        // itemId 가지고 상품 이미지 리스트 엔티티 객체 정보 불러옴
        List<ItemImg> itemImgList = itemImgRepository
                .findByItemIdOrderByIdAsc(itemId);

        // 상품 이미지 DTO 그릇 생성
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();

        // 상품 이미지 엔티티 객체를 상품 이미지 DTO 객체로 변환
        for (ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            // 상품 이미지 DTO 그릇에다가 때려붓기
            itemImgDtoList.add(itemImgDto);
        }

        // 상품 id로 상품 엔티티 객체 뽑기
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        // 상품 엔티티 객체를 상품 DTO 객체로 변환
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        // 상품 DTO 객체에다가 상품 이미지 DTO 리스트를 박아넣음
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto,
                           List<MultipartFile> itemImgFileList) throws Exception {

        // 상품 수정
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);

        // 상품 이미지 수정
        List<Long> itemImgIds = itemFormDto.getItemImgIds();
        for (int i = 0; i < itemImgFileList.size(); i++) {
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }
        return item.getId();
    }
}
