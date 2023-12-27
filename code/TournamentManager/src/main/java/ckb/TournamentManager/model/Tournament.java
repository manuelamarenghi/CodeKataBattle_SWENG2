package ckb.TournamentManager.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;


@Data
@Entity
@Table(name= "Tournaments")

public class Tournament {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(name= "reg_deadline")
    private Date reg_deadline;

    public Tournament() {

    }

    public Long getId() {
        return id;
    }

    public Date getReg_deadline() {
        return reg_deadline;
    }

    public void setReg_deadline(Date reg_deadline) {
        this.reg_deadline = reg_deadline;
    }


}