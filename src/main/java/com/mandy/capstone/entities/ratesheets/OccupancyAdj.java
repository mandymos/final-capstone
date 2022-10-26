package com.mandy.capstone.entities.ratesheets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "occupancy_adj")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OccupancyAdj {
    @Id
    private String occupancy;
    private double ltv60;
    private double ltv70;
    private double ltv75;
    private double ltv80;
    private double ltv85;
    private double ltv90;
    private double ltv95;
    
}