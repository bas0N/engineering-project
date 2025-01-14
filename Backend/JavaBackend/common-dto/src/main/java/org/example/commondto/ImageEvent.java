package org.example.commondto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageEvent {
    private String thumb;
    private String large;
    private String variant;
    private String hiRes;
}
