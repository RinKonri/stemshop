package com.example.stemshop.dto.admin;

import java.util.Set;
public record UpdateUserRolesRequest(Set<String> roles) {}