// See LICENSE for license details.
package sifive.fpgashells.ip.xilinx

import Chisel._
import chisel3.core.{Input, Output, attach}
import chisel3.experimental.{Analog}
import freechips.rocketchip.util.{ElaborationArtefacts}

import sifive.blocks.devices.pinctrl.{BasePin}
import sifive.fpgashells.clocks._

//========================================================================
// This file contains common devices used by our Xilinx FPGA flows and some
// BlackBox modules used in the Xilinx FPGA flows
//========================================================================

//-------------------------------------------------------------------------
// mmcm
//-------------------------------------------------------------------------
/** mmcm: This is generated by the Xilinx IP Generation Scripts */

class mmcm extends BlackBox {
  val io = new Bundle {
    val clk_in1  = Input(Clock())
    val clk_out1 = Output(Clock())
    val clk_out2 = Output(Clock())
    val clk_out3 = Output(Clock())
    val resetn   = Input(Bool())
    val locked   = Output(Bool())
  }
}

//-------------------------------------------------------------------------
// reset_sys
//-------------------------------------------------------------------------
/** reset_sys: This is generated by the Xilinx IP Generation Scripts */

class reset_sys extends BlackBox {
  val io = new Bundle {
    val slowest_sync_clk     = Input(Clock())
    val ext_reset_in         = Input(Bool())
    val aux_reset_in         = Input(Bool())
    val mb_debug_sys_rst     = Input(Bool())
    val dcm_locked           = Input(Bool())
    val mb_reset             = Output(Bool())
    val bus_struct_reset     = Output(Bool())
    val peripheral_reset     = Output(Bool())
    val interconnect_aresetn = Output(Bool())
    val peripheral_aresetn   = Output(Bool())
  }
}

//-------------------------------------------------------------------------
// reset_mig
//-------------------------------------------------------------------------
/** reset_mig: This is generated by the Xilinx IP Generation Scripts */

class reset_mig extends BlackBox {
  val io = new Bundle {
    val slowest_sync_clk     = Input(Clock())
    val ext_reset_in         = Input(Bool())
    val aux_reset_in         = Input(Bool())
    val mb_debug_sys_rst     = Input(Bool())
    val dcm_locked           = Input(Bool())
    val mb_reset             = Output(Bool())
    val bus_struct_reset     = Output(Bool())
    val peripheral_reset     = Output(Bool())
    val interconnect_aresetn = Output(Bool())
    val peripheral_aresetn   = Output(Bool())
  }
}

//-------------------------------------------------------------------------
// PowerOnResetFPGAOnly
//-------------------------------------------------------------------------
/** PowerOnResetFPGAOnly -- this generates a power_on_reset signal using
  * initial blocks.  It is synthesizable on FPGA flows only.
  */

// This is a FPGA-Only construct, which uses
// 'initial' constructions
class PowerOnResetFPGAOnly extends BlackBox {
  val io = new Bundle {
    val clock = Input(Clock())
    val power_on_reset = Output(Bool())
  }
}

object PowerOnResetFPGAOnly {
  def apply (clk: Clock): Bool = {
    val por = Module(new PowerOnResetFPGAOnly())
    por.io.clock := clk
    por.io.power_on_reset
  }
}


//-------------------------------------------------------------------------
// vc707_sys_clock_mmcm
//-------------------------------------------------------------------------
//IP : xilinx mmcm with "NO_BUFFER" input clock
class Series7MMCM(c : PLLParameters) extends BlackBox with PLL {
  val io = new Bundle {
    val clk_in1   = Bool(INPUT)
    val clk_out1  = if (c.req.size >= 1) Some(Clock(OUTPUT)) else None
    val clk_out2  = if (c.req.size >= 2) Some(Clock(OUTPUT)) else None
    val clk_out3  = if (c.req.size >= 3) Some(Clock(OUTPUT)) else None
    val clk_out4  = if (c.req.size >= 4) Some(Clock(OUTPUT)) else None
    val clk_out5  = if (c.req.size >= 5) Some(Clock(OUTPUT)) else None
    val clk_out6  = if (c.req.size >= 6) Some(Clock(OUTPUT)) else None
    val clk_out7  = if (c.req.size >= 7) Some(Clock(OUTPUT)) else None
    val reset     = Bool(INPUT)
    val locked    = Bool(OUTPUT)
  }
  
  val moduleName = c.name
  override def desiredName = c.name

  def getClocks = Seq() ++ io.clk_out1 ++ io.clk_out2 ++ 
                           io.clk_out3 ++ io.clk_out4 ++ 
                           io.clk_out5 ++ io.clk_out6 ++ 
                           io.clk_out7
  
  def getLocked = io.locked
  def getClockNames = Seq.tabulate (c.req.size) { i =>
    s"${c.name}/inst/mmcm_adv_inst/CLKOUT${i}" 
  }
 
  var elaborateArtefactsString = ""
  var elaborateArtefactsString_temp = ""
  for (i <- 0 until 7) {
    elaborateArtefactsString_temp += (
      if (i < c.req.size) 
        {s""" CONFIG.CLKOUT${(i+1).toString}_USED {true} \\
        |""".stripMargin} 
      else 
        {s""" CONFIG.CLKOUT${(i+1).toString}_USED {false} \\
        |""".stripMargin})
  }

  for (i <- 0 until c.req.size) {
    val freq = c.req(i).freqMHz.toString()
    val phase =  c.req(i).phaseDeg.toString()
    val dutyCycle = c.req(i).dutyCycle.toString()


    elaborateArtefactsString_temp += 
      s""" CONFIG.CLKOUT${(i+1).toString}_REQUESTED_OUT_FREQ {${freq}} \\
      | CONFIG.CLKOUT${(i+1).toString}_REQUESTED_PHASE {${phase}} \\
      | CONFIG.CLKOUT${(i+1).toString}_REQUESTED_DUTY_CYCLE {${dutyCycle}} \\
      |""".stripMargin
    }
    
    elaborateArtefactsString += (
      s"""create_ip -name clk_wiz -vendor xilinx.com -library ip -module_name \\
      | ${moduleName} -dir $$ipdir -force 
      | set_property -dict [list \\
      | CONFIG.CLK_IN1_BOARD_INTERFACE {Custom} \\
      | CONFIG.PRIM_SOURCE {No_buffer} \\
      | CONFIG.NUM_OUT_CLKS {${c.req.size.toString}} \\
      | CONFIG.PRIM_IN_FREQ {${c.input.freqMHz.toString}} \\
      | CONFIG.CLKIN1_JITTER_PS {${c.input.jitter}} \\
      | ${elaborateArtefactsString_temp}
      | ] [get_ips ${moduleName}]""").stripMargin

  ElaborationArtefacts.add(
    s"${moduleName}.vivado.tcl",
    elaborateArtefactsString)
}

//-------------------------------------------------------------------------
// vc707reset
//-------------------------------------------------------------------------

class vc707reset() extends BlackBox
{
  val io = new Bundle{
    val areset = Bool(INPUT)
    val clock1 = Clock(INPUT)
    val reset1 = Bool(OUTPUT)
    val clock2 = Clock(INPUT)
    val reset2 = Bool(OUTPUT)
    val clock3 = Clock(INPUT)
    val reset3 = Bool(OUTPUT)
    val clock4 = Clock(INPUT)
    val reset4 = Bool(OUTPUT)
  }
}

//-------------------------------------------------------------------------
// sdio_spi_bridge
//-------------------------------------------------------------------------

class sdio_spi_bridge() extends BlackBox
{
  val io = new Bundle{
    val clk      = Clock(INPUT)
    val reset    = Bool(INPUT)
    val sd_cmd   = Analog(1.W)
    val sd_dat   = Analog(4.W)
    val spi_sck  = Bool(INPUT)
    val spi_cs   = Bool(INPUT)
    val spi_dq_o = Bits(INPUT,4)
    val spi_dq_i = Bits(OUTPUT,4)
  }
}
