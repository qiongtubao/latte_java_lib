package latte.lib.tikv.commiter;

public interface Committer {
    void prewrite(int backOfferMs);
    void commit(int backOfferMs);
}
