package com.mrbprakash;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text")
    private String title;
    private Long sequence;

    @Column(columnDefinition = "text")
    private String summary;

    private String provider;
    
    @Column(columnDefinition = "text")
    private String url;

    @Override
    public String toString() {
        return "News [title=" + title + "]";
    }
}
