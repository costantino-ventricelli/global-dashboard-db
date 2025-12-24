package com.globaldashboard.db.entity;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@MappedEntity("widgets")
public class Widget {
        @Id
        @GeneratedValue(GeneratedValue.Type.AUTO)
        private Long id;

        private Long dashboardId;
        private String title;
        private String type;

        @TypeDef(type = DataType.JSON)
        private String configJson;

        private Integer positionX;
        private Integer positionY;
        private Integer width;
        private Integer height;
}
