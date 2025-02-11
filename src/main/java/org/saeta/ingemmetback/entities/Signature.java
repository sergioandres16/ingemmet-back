package org.saeta.ingemmetback.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "signature")
public class Signature {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "signature_id_seq")
    @SequenceGenerator(name = "signature_id_seq", sequenceName = "public.signature_id_seq", allocationSize = 1)
    private Integer id;

    @Column(nullable = false, length = 12, unique = true)
    private String dni;

    @Column(nullable = false)
    private String rutafir;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private LocalDateTime fechahora;
}