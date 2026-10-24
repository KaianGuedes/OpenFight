package yesman.epicfight.client.gui.compat;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
public class FilteredEditBox extends yesman.epicfight.client.gui.widgets.FilteredEditBox {
 public FilteredEditBox(Font f,Component m){super(f,150,20,m);}
 public FilteredEditBox(Font f,int w,int h,Component m){super(f,w,h,m);}
 public FilteredEditBox(Font f,int x,int y,int w,int h,Component m){super(f,x,y,w,h,m);}
 public FilteredEditBox(Font f,int x,int y,int w,int h,EditBox old,Component m){super(f,x,y,w,h,m);if(old!=null)this.setValue(old.getValue());}
}
