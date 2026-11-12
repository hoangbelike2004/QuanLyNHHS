
package com.QuanLyNHHS.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "CT_HOA_DON")
public class CTHoaDon {

    @EmbeddedId
    private CTHoaDonId id;

    @Column(name = "SO_LUONG", precision = 10, scale = 2)
    private BigDecimal soLuong;

    @Column(name = "DON_GIA", precision = 12, scale = 2)
    private BigDecimal donGia;

    @Column(name = "THANH_TIEN", precision = 14, scale = 2)
    private BigDecimal thanhTien;

    public CTHoaDon() {
    }

    public CTHoaDonId getId() {
        return id;
    }

    public void setId(CTHoaDonId id) {
        this.id = id;
    }

    public BigDecimal getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(BigDecimal soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

    public Long getMaHd() {
        return id == null ? null : id.getMaHd();
    }

    public Long getMaMon() {
        return id == null ? null : id.getMaMon();
    }

    @Embeddable
    public static class CTHoaDonId implements Serializable {

        private static final long serialVersionUID = 1L;

        @Column(name = "MA_HD")
        private Long maHd;

        @Column(name = "MA_MON")
        private Long maMon;

        public CTHoaDonId() {
        }

        public CTHoaDonId(Long maHd, Long maMon) {
            this.maHd = maHd;
            this.maMon = maMon;
        }

        public Long getMaHd() {
            return maHd;
        }

        public void setMaHd(Long maHd) {
            this.maHd = maHd;
        }

        public Long getMaMon() {
            return maMon;
        }

        public void setMaMon(Long maMon) {
            this.maMon = maMon;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (!(obj instanceof CTHoaDonId other)) {
                return false;
            }

            return Objects.equals(maHd, other.maHd)
                    && Objects.equals(maMon, other.maMon);
        }

        @Override
        public int hashCode() {
            return Objects.hash(maHd, maMon);
        }
    }
}
