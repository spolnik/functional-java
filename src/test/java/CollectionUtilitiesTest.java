import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static io.functional.CollectionUtilities.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class CollectionUtilitiesTest {

    @Test
    public void empty_list() {
        assertThat(list()).hasSize(0);
    }

    @Test
    public void single_item_list() {
        List<String> singleItemList = list("item");
        assertThat(singleItemList).hasSize(1);
        assertThat(singleItemList.get(0)).isEqualTo("item");
    }

    @Test
    public void list_from_list() {
        List<String> listFromList = list(list("item", "item2"));
        assertThat(listFromList).hasSize(2);
        assertThat(listFromList.get(0)).isEqualTo("item");
        assertThat(listFromList.get(1)).isEqualTo("item2");
    }

    @Test
    public void vararg_list() {
        List<String> varargList = list("item", "item2");
        assertThat(varargList).hasSize(2);
        assertThat(varargList.get(0)).isEqualTo("item");
        assertThat(varargList.get(1)).isEqualTo("item2");
    }

    @Test
    public void head_list() {
        String result = head(list("item", "item2"));
        assertThat(result).isEqualTo("item");
    }

    @Test(expected = IllegalStateException.class)
    public void head_of_empty_list() {
        head(list());
    }

    @Test
    public void tail_list() {
        List<String> result = tail(list("item", "item2", "item3"));
        assertThat(result).hasSameElementsAs(list("item2", "item3"));
    }

    @Test(expected = IllegalStateException.class)
    public void tail_of_empty_list() {
        tail(list());
    }

    @Test
    public void copy_list() {
        List<String> list = list("item", "item2");
        List<String> copy = copy(list);

        assertThat(copy).hasSameElementsAs(list);
    }

    @SuppressWarnings("all")
    @Parameters(method = "lists")
    @Test(expected = UnsupportedOperationException.class)
    public void empty_list_immutability(List<Object> list) {
        list.add(new Object());
    }

    @SuppressWarnings("unused")
    private Object lists() {
        return new Object[] {
                list(),
                list(new Object()),
                list(new Object(), new Object()),
                list(list(new Object())),
                copy(list(new Object()))
        };
    }
}
