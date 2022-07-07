/**
 * Bean Stores HeadLine Information
 *
 * @author Renato Ponce
 * @version 1.0
 * @since 2022-06-24
 */

package com.nttdata.apiresttransactions.dto;

import lombok.Data;

@Data
public class HeadLineDto {
    private String name;
    private String lastname;
    private String numberDocument;
    private String email;
}
