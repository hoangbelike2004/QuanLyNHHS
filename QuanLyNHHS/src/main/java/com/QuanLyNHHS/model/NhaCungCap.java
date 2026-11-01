package com.QuanLyNHHS.model;

import jakarta.persistence.*;

@Entity
@Table(name = "NHA_CUNG_CAP")
public class NhaCungCap {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ncc_seq")
    @SequenceGenerator(name = "ncc_seq", sequenceName = "SEQ_NHA_CUNG_CAP", allocationSize = 1)
    @Column(name = "MA_NCC")
    private Long maNcc;

    @Column(name = "TEN_NCC", nullable = false, length = 150)
    private String tenNcc;

    @Column(name = "SDT", length = 15)
    private String sdt;

    @Column(name = "DIA_CHI", length = 200)
    private String diaChi;

    @Column(name = "EMAIL", length = 100)
    private String email;

    public Long getMaNcc() { return maNcc; }
    public void setMaNcc(Long maNcc) { this.maNcc = maNcc; }

    public String getTenNcc() { return tenNcc; }
    public void setTenNcc(String tenNcc) { this.tenNcc = tenNcc; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
