package com.rental.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
@Entity
@Table(name = "image_details")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_details_id", nullable = false)
    private HouseDetails houseDetails;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "image_size", nullable = false)
    private double imageSize;

    @Column(name ="image_name", nullable = false)
    private String imageName;
}
