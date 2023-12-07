package com.paras.plantuml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class UmlPlant {
    private StringBuilder builder;
    private Collection<UmlDiagram> diagrams;

    public UmlPlant() {
        builder = new StringBuilder();
        diagrams = new ArrayList<>();
    }

    public UmlSequenceDiagram createSequenceDiagram() {
        UmlSequenceDiagram sd = new UmlSequenceDiagram();
        diagrams.add(sd);
        return sd;
    }

    public UmlClassDiagram createClassDiagram() {
        UmlClassDiagram cd = new UmlClassDiagram();
        diagrams.add(cd);
        return cd;
    }

    public String toUml() {
        return 
            builder
            .append("@startUml\n")
            .append(
                diagrams
                    .stream()
                    .map(UmlDiagram::toUml)
                    .collect(Collectors.joining())
            )
            .append("@endUml")
            .toString();
    }
    
}
