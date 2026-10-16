package com.QuanLyNHHS.controller;

import com.QuanLyNHHS.model.NhanVien;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/nhan-vien")
public class NhanVienController {

    private List<NhanVien> getSampleStaff() {
        List<NhanVien> list = new ArrayList<>();
        list.add(new NhanVien("NV01", "Nguyễn Cao Hoàng", "0912 345 678", "hoang.nguyen@nhhs.vn", 
                "Quản lý nhà hàng", "Toàn thời gian", "10/01/2022", 22000000, "Đang làm việc"));
        
        list.add(new NhanVien("NV02", "Trần Văn Hải", "0988 123 456", "hai.tran@nhhs.vn", 
                "Bếp trưởng", "Ca tối (14:00 - 22:30)", "15/03/2022", 20000000, "Đang làm việc"));

        list.add(new NhanVien("NV03", "Lê Thị Thu Thảo", "0977 654 321", "thao.le@nhhs.vn", 
                "Thu ngân trưởng", "Ca sáng (07:30 - 15:30)", "05/06/2023", 11000000, "Đang làm việc"));

        list.add(new NhanVien("NV04", "Hoàng Minh Tuấn", "0905 889 911", "tuan.hoang@nhhs.vn", 
                "Đầu bếp món nướng", "Ca tối (14:00 - 22:30)", "20/08/2023", 14000000, "Đang làm việc"));

        list.add(new NhanVien("NV05", "Phạm Quỳnh Anh", "0934 567 890", "quynhanh.pham@nhhs.vn", 
                "Lễ tân & Chăm sóc khách hàng", "Ca sáng (08:00 - 16:30)", "12/11/2023", 9500000, "Đang làm việc"));

        list.add(new NhanVien("NV06", "Vũ Đức Anh", "0944 222 333", "ducanh.vu@nhhs.vn", 
                "Nhân viên phục vụ", "Ca tối (16:00 - 23:00)", "02/02/2024", 8000000, "Đang làm việc"));

        list.add(new NhanVien("NV07", "Đỗ Văn Long", "0918 777 666", "long.do@nhhs.vn", 
                "Nhân viên phục vụ", "Ca sáng (08:00 - 16:00)", "18/03/2024", 8000000, "Nghỉ phép"));

        list.add(new NhanVien("NV08", "Nguyễn Thị Mai", "0966 333 888", "mai.nguyen@nhhs.vn", 
                "Thu ngân", "Ca tối (15:00 - 23:00)", "01/04/2024", 9000000, "Đang làm việc"));

        return list;
    }

    @GetMapping
    public String index(Model model) {
        List<NhanVien> staffList = getSampleStaff();
        long totalStaff = staffList.size();
        long activeStaff = staffList.stream().filter(s -> "Đang làm việc".equals(s.getTrangThai())).count();
        long onLeaveStaff = staffList.stream().filter(s -> "Nghỉ phép".equals(s.getTrangThai())).count();

        model.addAttribute("staffList", staffList);
        model.addAttribute("totalStaff", totalStaff);
        model.addAttribute("activeStaff", activeStaff);
        model.addAttribute("onLeaveStaff", onLeaveStaff);
        model.addAttribute("activeMenu", "nhanvien");
        return "nhanvien/index";
    }
}
