package com.QuanLyNHHS.controller;

import com.QuanLyNHHS.model.MonAn;
import com.QuanLyNHHS.model.LoaiMon;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/mon-an")
public class MonAnController {

    private List<LoaiMon> getSampleCategories() {
        List<LoaiMon> list = new ArrayList<>();
        list.add(new LoaiMon("LM01", "Tôm & Cua ghẹ", "Các loại tôm hùm, cua huỳnh đế, ghẹ xanh tươi sống", 6, "Hoạt động"));
        list.add(new LoaiMon("LM02", "Cá hải sản", "Cá mú, cá song, cá hồi tươi nhập khẩu và đánh bắt tự nhiên", 5, "Hoạt động"));
        list.add(new LoaiMon("LM03", "Ốc & Sò biển", "Ốc hương, tu hài, sò dương, sò mai nướng thơm ngon", 8, "Hoạt động"));
        list.add(new LoaiMon("LM04", "Mực & Bạch tuộc", "Mực trứng, mực lá câu đêm, bạch tuộc nhúng giấm", 4, "Hoạt động"));
        list.add(new LoaiMon("LM05", "Lẩu & Nướng", "Lẩu thuyền chài chua cay, lẩu hải sản hoàng gia", 3, "Hoạt động"));
        list.add(new LoaiMon("LM06", "Đồ uống & Rượu vang", "Rượu vang trắng hải sản, nước ép tươi, bia tươi", 6, "Hoạt động"));
        return list;
    }

    private List<MonAn> getSampleDishes() {
        List<MonAn> list = new ArrayList<>();
        list.add(new MonAn("MA01", "Tôm Hùm Alaska Hấp Rượu Vang", "LM01", "Tôm & Cua ghẹ", "Con (~1.2kg)", 
                1450000, "https://images.unsplash.com/photo-1553240799-36bbf332a5c3?auto=format&fit=crop&w=600&q=80", 
                "Tôm hùm Alaska nhập khẩu sống tươi nguyên con, hấp bia hoặc rượu vang trắng giữ trọn vị ngọt tự nhiên", "Đang phục vụ", 142));
        
        list.add(new MonAn("MA02", "Cua Hoàng Đế King Crab Sốt Bơ Tỏi", "LM01", "Tôm & Cua ghẹ", "Kg", 
                2200000, "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=600&q=80", 
                "Cua hoàng đế sốt bơ tỏi thơm ngậy đặc trưng kiểu Pháp, thịt cua chắc ngọt đượm vị", "Đang phục vụ", 89));

        list.add(new MonAn("MA03", "Cá Mú Đỏ Hấp Xì Dầu Hồng Kông", "LM02", "Cá hải sản", "Con (~1kg)", 
                750000, "https://images.unsplash.com/photo-1534482421-64566f976cfa?auto=format&fit=crop&w=600&q=80", 
                "Cá mú đỏ bơi bể hấp xì dầu thượng hạng cùng hành hoa, thì là giòn ngọt chuẩn vị ẩm thực Quảng Đông", "Đang phục vụ", 215));

        list.add(new MonAn("MA04", "Hàu Sữa Nướng Phô Mai Mozzarella", "LM03", "Ốc & Sò biển", "Đĩa 6 con", 
                240000, "https://images.unsplash.com/photo-1625944525533-473f1a3d54e7?auto=format&fit=crop&w=600&q=80", 
                "Hàu sữa tươi mập nướng cùng phô mai Pháp kéo sợi béo ngậy thơm lừng", "Đang phục vụ", 340));

        list.add(new MonAn("MA05", "Ốc Hương Rang Muối Tuyết Biển", "LM03", "Ốc & Sò biển", "Đĩa (300g)", 
                320000, "https://images.unsplash.com/photo-1615141982883-c7ad0e69fd62?auto=format&fit=crop&w=600&q=80", 
                "Ốc hương size lớn tươi giòn sần sật, rang muối tuyết cay the đậm đà kích thích vị giác", "Đang phục vụ", 280));

        list.add(new MonAn("MA06", "Mực Trứng Hấp Gừng Ớt", "LM04", "Mực & Bạch tuộc", "Đĩa", 
                290000, "https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?auto=format&fit=crop&w=600&q=80", 
                "Mực trứng câu Cát Bà hấp cùng gừng tươi và đầu hành, ngọt lịm chấm muối ớt xanh Nha Trang", "Đang phục vụ", 195));

        list.add(new MonAn("MA07", "Lẩu Hải Sản Hoàng Gia Thuyền Chài", "LM05", "Lẩu & Nướng", "Nồi (4-6 người)", 
                980000, "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?auto=format&fit=crop&w=600&q=80", 
                "Nước lẩu chua cay Tomyum đặc chế từ cốt xương hải sản, đầy ắp tôm sú, mực lá, cá bớp, nghêu hoa", "Đang phục vụ", 175));

        list.add(new MonAn("MA08", "Tu Hài Canada Nướng Mỡ Hành", "LM03", "Ốc & Sò biển", "Con", 
                185000, "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?auto=format&fit=crop&w=600&q=80", 
                "Tu hài Canada thịt giòn ngọt độc đáo, nướng mỡ hành rắc đậu phộng thơm phức", "Tạm hết", 62));

        return list;
    }

    @GetMapping
    public String index(Model model) {
        List<MonAn> dishes = getSampleDishes();
        List<LoaiMon> categories = getSampleCategories();
        
        long totalDishes = dishes.size();
        long servingDishes = dishes.stream().filter(d -> "Đang phục vụ".equals(d.getTrangThai())).count();
        long outOfStockDishes = dishes.stream().filter(d -> "Tạm hết".equals(d.getTrangThai())).count();

        model.addAttribute("dishes", dishes);
        model.addAttribute("categories", categories);
        model.addAttribute("totalDishes", totalDishes);
        model.addAttribute("servingDishes", servingDishes);
        model.addAttribute("outOfStockDishes", outOfStockDishes);
        model.addAttribute("activeMenu", "monan");
        return "monan/index";
    }
}
