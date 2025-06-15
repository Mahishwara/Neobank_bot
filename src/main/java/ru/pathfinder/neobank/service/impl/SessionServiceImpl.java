package ru.pathfinder.neobank.service.impl;

import org.springframework.stereotype.Service;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.service.SessionService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class SessionServiceImpl implements SessionService {

    private final ConcurrentMap<Long, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public Session getSession(Long userId) {
        return sessions.computeIfAbsent(userId, k -> new Session(userId));
    }

    @Override
    public void removeSession(Session session) {
        sessions.remove(session.getUserId());
    }

}
