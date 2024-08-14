package com.fiso.nleya.marker.setup.assessment;

import java.util.List;

public record AssInvitationDTO(
        List<Assessee> assessees,
        String assessmentName,
        String assessorName,
        String code,
        String date
){

}
