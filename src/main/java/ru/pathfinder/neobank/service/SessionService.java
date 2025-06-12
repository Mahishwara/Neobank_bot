package ru.pathfinder.neobank.service;

import org.springframework.stereotype.Service;
import ru.pathfinder.neobank.domain.Session;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class SessionService {

    private final ConcurrentMap<Long, Session> sessions = new ConcurrentHashMap<>();

    public Session getSession(Long userId) {
        return sessions.computeIfAbsent(userId, k -> new Session(userId));
    }

    public void clearSession(Session session) {
        session.clear();
        sessions.remove(session.getUserId());
    }

}
