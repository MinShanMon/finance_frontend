package com.personalfinanceapp.frontend.model;


import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloseTicket {
  private String decision;

  @Override
  public String toString() {
    return "TicketStatus [decision=" + decision + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(decision);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CloseTicket other = (CloseTicket) obj;
    return Objects.equals(decision, other.decision);
  }

}
