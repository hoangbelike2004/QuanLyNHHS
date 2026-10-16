package com.QuanLyNHHS.controller;

import com.QuanLyNHHS.model.LoaiMon;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/loai-mon")
public class LoaiMonController {

    private List<LoaiMon> getSampleCategories() {
        List<LoaiMon> list = new ArrayList<>();
        list.add(new LoaiMon("LM01", "Tôm & Cua ghẹ", "Các loại tôm hùm Alaska, cua huỳnh đế, ghẹ xanh tươi sống", 6, "Hoạt động"));
        list.add(new LoaiMon("LM02", "Cá hải sản", "Cá mú, cá song, cá hồi tươi nhập khẩu và đánh bắt tự nhiên", 5, "Hoạt động"));
        list.add(new LoaiMon("LM03", "Ốc & Sò biển", "Ốc hương, tu hài, sò dương, sò mai nướng thơm ngon", 8, "Hoạt động"));
        list.add(new LoaiMon("LM04", "Mực & Bạch tuộc", "Mực trứng, mực lá câu đêm, bạch tuộc nhúng giấm", 4, "Hoạt động"));
        list.add(new LoaiMon("LM05", "Lẩu & Nướng", "Lẩu thuyền chài chua cay, lẩu hải sản hoàng gia", 3, "Hoạt động"));
        list.add(new LoaiMon("LM06", "Đồ uống & Rượu vang", "Rượu vang trắng hải sản, nước ép tươi, bia tươi", 6, "Hoạt động"));
        return list;
    }

    @GetMapping
    public String index(Model model) {
        List<LoaiMon> categories = getSampleCategories();
        int totalCategories = categories.size();
        int totalDishesInCategories = categories.stream().mapToInt(LoaiMon::getSoLuongMon).sum();

        model.addAttribute("categories", categories);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("totalDishesInCategories", totalDishesInCategories);
        model.addAttribute("activeMenu", "loaimon");
        return "loaimon/index";
    }
}
