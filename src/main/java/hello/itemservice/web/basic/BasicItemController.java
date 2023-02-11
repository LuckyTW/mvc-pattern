package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {

    private final ItemRepository itemRepository;

//    @Autowired => 생성자 하나인 경우 생략가능
//    public BasicItemController(ItemRepository itemRepository) { // @RequiredArgsConstructor 애너테이션이 있으면 final이 붙은 속성값을 가지고 생성자 만들어줌. 생략가능
//        this.itemRepository = itemRepository;
//    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }

    // 특정 아이템 조회
    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

    // 아이템을 주문했을 때
    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }


//    @PostMapping("/add")
    public String addItemV1(@RequestParam String itemName,
                       @RequestParam int price,
                       @RequestParam Integer quantity,
                       Model model) {

        // 전달된 아이템 정보로 아이템 객체 생성
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        // 데이터베이스 저장
        itemRepository.save(item);

        //모델에 넣기
        model.addAttribute("item", item);

        return "basic/item";
    }

    // addItemV2 -> modelAttribute 사용
//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item, // ModelAttribute가 자동으로 아이템 객체를 자동으로 만들어줌
                            Model model) {

        // 전달된 아이템 정보로 아이템 객체 생성
//        Item item = new Item();
//        item.setItemName(itemName);
//        item.setPrice(price);
//        item.setQuantity(quantity);

        // 데이터베이스 저장
        itemRepository.save(item);

        //모델에 넣기
//        model.addAttribute("item", item);  -> 이것도 자동으로 만들어줘서 생략이 가능
        return "basic/item";
    }

    //V3 -> PRG 구현
//    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item) // ModelAttribute가 자동으로 아이템 객체를 자동으로 만들어줌
    {
        // 데이터베이스 저장
        itemRepository.save(item);
        //redirect
        return "redirect:/basic/items/" + item.getId();
    }

    //V4 -> redirectAttribute 사용
    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, RedirectAttributes redirectAttributes) // ModelAttribute가 자동으로 아이템 객체를 자동으로 만들어줌
    {
        // 데이터베이스 저장
        Item savedItem = itemRepository.save(item);

        //redirectAttribute에 저장
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        //redirect
        return "redirect:/basic/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId,
                       @ModelAttribute Item item
                       ){
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}"; // redirect
    }


    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init() {
        itemRepository.save(new Item("쉬운 성경", 10000, 10));
        itemRepository.save(new Item("머그잔", 20000, 20));
    }


}
