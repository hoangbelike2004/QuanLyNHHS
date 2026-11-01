package com.QuanLyNHHS.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CTPhieuNhapId implements Serializable {
    @Column(name = "MA_PN")
    private Long maPn;

    @Column(name = "MA_NL")
    private Long maNl;

    public CTPhieuNhapId() {}

    public CTPhieuNhapId(Long maPn, Long maNl) { this.maPn = maPn; this.maNl = maNl; }

    @Override public boolean equals(Object o) {
        return o instanceof CTPhieuNhapId other && Objects.equals(maPn, other.maPn) && Objects.equals(maNl, other.maNl);
    }
    @Override public int hashCode() { return Objects.hash(maPn, maNl); }

    public Long getMaPn() { return maPn; }
    public void setMaPn(Long maPn) { this.maPn = maPn; }

    public Long getMaNl() { return maNl; }
    public void setMaNl(Long maNl) { this.maNl = maNl; }
}
