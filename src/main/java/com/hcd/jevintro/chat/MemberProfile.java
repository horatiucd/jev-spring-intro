package com.hcd.jevintro.chat;

import com.hcd.jevintro.jev.dto.Answer;

/**
 * Jev's judgments about one chat member.
 *
 * @param role        Choice - the member's main role in the group
 * @param toxic       Noul - probability the member was hostile towards others
 * @param unanswered  Noul - probability the member still has an unanswered question
 * @param helpfulness Score - from 0 (no help) to 3 (consistently solves problems)
 */
public record MemberProfile(String member,
                            Answer.Choice role,
                            Answer.Noul toxic,
                            Answer.Noul unanswered,
                            Answer.Score helpfulness) {
}
