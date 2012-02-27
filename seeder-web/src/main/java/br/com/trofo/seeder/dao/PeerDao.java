/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.trofo.seeder.dao;

import br.com.trofo.seeder.entity.Peer;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Andoreh
 */
public class PeerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void persistEntity(Peer peer) {
        Peer existingPeer = findPeer(peer.getInfoHash(),peer.getIp(),peer.getPort());
        if(existingPeer == null){
            entityManager.persist(peer);
        }
        else{
            existingPeer.setExpires(peer.getExpires());
            existingPeer.setPeerId(peer.getPeerId());
            entityManager.merge(existingPeer);
        }

    }

    public Collection<Peer> getPeers(Peer requestingPeer, int numWant) {
        Query query = entityManager.createQuery("from Peer where infoHash = :infoHash and expires > :expires and not (port = :port and ip = :ip)");
        query.setParameter("infoHash", requestingPeer.getInfoHash());
        query.setParameter("port", requestingPeer.getPort());
        query.setParameter("ip", requestingPeer.getIp());
        query.setParameter("expires", new Date());
        query.setMaxResults(numWant);
        return query.getResultList();
    }

    private Peer findPeer(String infoHash, String ip, Integer port) {
        Query query = entityManager.createQuery("from Peer where infoHash = :infoHash and port = :port and ip = :ip");
        query.setParameter("infoHash", infoHash);
        query.setParameter("port", port);
        query.setParameter("ip", ip);
        List resultList = query.getResultList();
        if(resultList.isEmpty()){
            return null;
        }
        return (Peer) resultList.get(0);
    }
}
